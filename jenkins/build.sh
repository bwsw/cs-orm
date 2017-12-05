#!/bin/bash -e

echo "---------------------------------------------"
echo "----------------- Unit tests ----------------"
echo "---------------------------------------------"

sbt clean coverage test coverageReport

echo "---------------------------------------------"
echo "-------------- Scalastyle checks ------------"
echo "---------------------------------------------"

sbt scalastyle

sbt test:scalastyle
