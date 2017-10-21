#!/bin/bash

# http://localhost:8080/api/interlock/{assetId}/rfids/{rfid}
curl \
  --user api:api \
  http://localhost:8080/api/interlock/2/rfids/rfid-badge-0
  
  