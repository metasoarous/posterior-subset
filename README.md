# posterior-subset

A simple Clojure/JVM CLI for subsetting large BEAST posterior files (logfiles and treefiles).

## Overview

When running BEAST in `-resume` mode, your posterior files can get quite large.
This can clog up analysis programs like Tracer and PACT.
To deal with this, you might want to take every N samples or so from these output files and use these subsets in downstream analyses.
However, this could be a touch messy depending on the exact fraction of samples you want to keep.
Imagine you have 18,457 samples and want 5,000 of them.
What do you do?
You likely don't want to think about how to evenly pick the samples you want, you just want to know that you're getting an even distribution when you specify how many samples you want to keep.
This tool - `posterior-subset` - solves this problem by

1. Counting the number of samples in your file
2. Computing from this the fraction of samples to keep given the desired number of output samples
3. Doing a second pass through the file where, for each sequence:
    a. If adding the sequence to the output file makes the ratio of sequences included closer to the goal ratio we include it
    b. If not, we leave it out

This gives us an evenly distributed selection of samples from the larger collection.

## Usage

To use, just download the prebuilt executable jar to somewhere in your PATH (e.g. `~/bin`).

    curl https://raw.githubusercontent.com/metasoarous/posterior-subset/master/posterior-subset > ~/bin/posterior-subset
    chmod +x ~/bin/posterior-subset

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

