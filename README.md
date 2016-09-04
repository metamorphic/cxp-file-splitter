CXP File Splitter
=================

This project implements a custom processor module named *cxp-file-splitter*. When deployed to a Spring XD container,
the module accepts a path to a data file and reads it line by line, wraps each line within a JSON message, and passes
it to the next module. The name of the data file is also transmitted as one of the JSON fields. The module is backed
by a java class *FileSplitter*.

## Building with Gradle

    ./gradlew clean build

## Using the Custom Module

The 'fat jar' will be in `<project-build-dir>/libs/cxp-file-splitter-1.0.jar`. To install and register the module to
your Spring XD distribution, use the `module upload` Spring XD shell command. Start Spring XD and the shell:

    xd:>module upload --file [path-to]/cxp-file-splitter-1.0.jar --name cxpFileSplitter --type processor

### Getting the metadata service running

Because the cxpStreamProcessor calls a metadata web service to obtain information about the JSON input records, the
metadata web service must be running before the cxpStreamProcessor module is called.

    cd <path-to>/metastore/
    java -jar build/libs/metastore-1.0.jar

Allow a few minutes for the metadata service to start completely before moving onto the next step.

### Testing the JSON output

Create and deploy a stream:

    xd:>stream create --name fileevents --definition "file --dir=[path-to-data-dir] --pattern=*.* --preventDuplicates=false --ref=true --outputType=text/plain | cxp-file-splitter | script --script=file:///home/cxp/big-data-cxp/cxp-ingest-stream/scripts/jsonPayload.groovy | cxp-stream-processor | log" --deploy

You should see the SQL output in the XD Server log messages.

    2015-05-12 17:20:33,416 1.1.1.RELEASE  INFO task-scheduler-7 sink.fileevents - INSERT INTO events (process_name, customer_id, customer_id_type_id, event_type_id, value, ts, created_ts) values ( ...

Run the following command to delete the test stream.

    xd:>stream destroy --name fileevents

### Loading to a Postgresql table

To insert data into the event log (e.g. Postgresql), use the `psql` command via a shell sink. This sink executes the
SQL statement in each message and, as a result, inserts records into the table.
	
	xd:>stream create --name fileevents --definition "file --dir=[path-to-data-dir] --pattern=*.* --preventDuplicates=true --ref=true --outputType=text/plain | cxp-file-splitter | script --script=file:///home/cxp/big-data-cxp/cxp-ingest-stream/scripts/jsonPayload.groovy | cxp-stream-processor | shell --command='psql cxpdev'" --deploy

Note that we used `preventDuplicates=true` to protect against loading the same data multiple times.
