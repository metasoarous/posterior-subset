(defproject posterior-subset "0.1.0-SNAPSHOT"
  :description "A simple Clojure/JVM CLI for subsetting BEAST posterior files (logfiles and treefiles)"
  :url "https://github.com/metasoarous/posterior-subset"
  :main posterior-subset.core
  :aot [posterior-subset.core]
  :bin {:name "posterior-subset"}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-bin "0.3.4"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]])
