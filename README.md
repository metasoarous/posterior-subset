# posterior-subset

A simple Clojure/JVM CLI for subsetting BEAST posterior files (logfiles and treefiles).

## Usage

To use, just download the prebuilt executable jar to somewhere in your PATH (e.g. `~/bin`).

    curl ...

To execute it, run `posterior-subset <command line args>`.
Full command line usage comes with the `-h` flag:

    > posterior-subset -h

    Rarefy a posterior treefile or logfile, as output by BEAST. Arguments and options are:

      [infile]
      [outfile]
      -c, --out-count COUNT        Approximate number of lines in output file
      -f, --out-fraction FRACTION  Approximate fraction of lines to keep in output file (not supported yet)
      -t, --file-type FTYPE        Type of file (either 'logfile' or 'treefile')
      -h, --help

## Development

Should you need to make modifications to this project, it's recommended you use Leiningen.
You can download leinengein [here](XXX).
Simply put that script somewhere in your path (`~/bin`, for example), and then run `lein` to have it download its dependencies.

    curl ... > ~/bin/lein
    chmod +x ~/bin/lein
    lein

Now clone this repository, run lein deps, and start developing.

    git clone ...
    cd posterior-subset
    lein deps
    vim src/posterior_subset/core.clj
    <code furiously>

To test out the CLI along the way, you can run `lein trampoline run <command line args>`.
Once you're happy with your changes, run `lein bin` to generate a new executable jar file at `target/posterior-subset`.

## License

Copyright © 2015 Christopher Small

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

