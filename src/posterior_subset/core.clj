(ns posterior-subset.core
  "A little library for subsetting BEAST posterior files"
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [clojure.java.io :as io])
  (:gen-class))


;; The regular expressions we use for deciding what kind of line we're working with
(def tree-line-regexp #"(?i)tree STATE_.*")
(def log-line-regexp #"(?i)^(sample|[^#]).*")
(def file-type-regexp-map
  {:treefile tree-line-regexp
   :logfile  log-line-regexp})


(defn count-lines
  "Count the number of lines in the file (a string path) which match the file-type regex. We need this
  to guide us as we decide whether to add each sequence in."
  [file file-type]
  (with-open [rdr (io/reader file)]
    (->> (line-seq rdr)
      (filter (partial re-matches (file-type-regexp-map file-type)))
      (count))))


(defn abs
  "Helper function for absolute value."
  [r]
  (Math/abs (float r)))


(defn ratio-tester
  "Logic for whether we should add the next line, based on whether we're close to the goal ratio or not,
  given the goal ratio, the number that have been left out so far, and the number that have been left in
  so far."
  [goal left-out left-in]
  (let [new-base (+ left-out left-in 1)
        closeness (fn [r] (abs (- goal r)))]
    (< (closeness (/ (inc left-in) new-base))
       (closeness (/ left-in new-base)))))


(defn line-writer
  "Writes out lines to wrt, given infile, actual-count of items in infile, and the desired count in outfile."
  [wtr infile actual-count desired-count]
  (with-open [rdr (io/reader infile)]
    (let [ratio-goal (/ desired-count actual-count)]
      (loop [left-out 0
             left-in  0
             lines-left (line-seq rdr)]
        (when (seq lines-left)
          (let [leave-in? (ratio-tester ratio-goal left-out left-in)]
            (if leave-in?
              (do
                (.write wtr (str (first lines-left) \newline))
                (recur left-out (inc left-in) (rest lines-left)))
              (recur (inc left-out) left-in (rest lines-left)))))))))


(defn rarefy-file
  "Basically the inner main function; does all the work of making the line count, spitting out any comment lines,
  then running the `line-writer`."
  [[infile outfile :as args] {:keys [out-count out-fraction file-type] :as opts}]
  (when out-fraction (throw "out-fraction not yet supported"))
  (with-open [wtr (io/writer outfile)]
    ; Write out the comment lines (or translation data for nex) at the top of the posterior file
    (with-open [rdr (io/reader infile)]
      (loop [lines-left (line-seq rdr)]
        (let [current-line (first lines-left)]
          ; Fork here on whether to write or not
          (when-not (re-matches (file-type-regexp-map file-type) current-line)
            (.write wtr (str current-line \newline))
            (recur (rest lines-left))))))
    (let [infile-line-count (count-lines infile file-type)]
      (line-writer wtr infile infile-line-count out-count))))


;; CLI management stuff
;; ====================

(def cli-options
  [["-c" "--out-count COUNT" "Approximate number of lines in output file" :parse-fn #(Integer/parseInt %)]
   ["-f" "--out-fraction FRACTION" "Approximate fraction of lines to keep in output file (not supported yet)" :parse-fn #(Float/parseFloat %)]
   ["-t" "--file-type FTYPE" "Type of file (either 'logfile' or 'treefile')" :parse-fn keyword]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Rarefy a posterior treefile or logfile, as output by BEAST. Arguments and options are:"
        ""
        "  [infile]"
        "  [outfile]"
        options-summary
        ""
        "See https://github.com/metasoarous/posterior-subset for more."]
    (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred in parsing:\n\n"
       (string/join \newline errors)))

(defn exit [status & msgs]
  (apply println msgs)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 2) (exit 1
                                       "Must specify infile and outfile args\n"
                                       (usage summary))
      errors (exit 1 (error-msg errors)))
    (rarefy-file arguments options)))

