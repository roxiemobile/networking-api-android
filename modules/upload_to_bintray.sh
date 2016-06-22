#!/bin/bash

./gradlew clean && ./gradlew install && ./gradlew bintrayUpload
