#!/bin/bash

if [ -z "$DB_ALIVE_URL" ]; then
	exec bin/sota-device_registry $@
else
	./wait-for-it.sh $DB_ALIVE_URL -s -t 120 && exec bin/sota-device_registry $@
fi
