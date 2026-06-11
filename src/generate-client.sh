#!/bin/bash

ag streaming.yaml \
  @asyncapi/websocket-client-template \
  -o generated-client