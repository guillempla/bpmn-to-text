#!/usr/bin/env python3

import sys
import json
import requests

## USAGE:  ./sample-client.py server-IP < text 

HOST = sys.argv[1]
LANGID = "50005"
FREELING = "60006"

# language identification service URL
langid = "http://"+HOST+":"+LANGID
# text analysis service URL
freeling = "http://"+HOST+":"+FREELING


def call_server(url, request) :
   # set query elements
   request_headers = {'Content-Type': 'application/json; charset=utf-8'}
   request_data = json.dumps(request, ensure_ascii=False).encode('utf-8')
   
   # Send request and get response    
   resp = requests.post(url, data=request_data, headers=request_headers)
   # HTTP error, raise exception
   if resp.status_code != requests.codes.ok : 
      print("ERROR", resp.status_code, "- Reason:", resp.headers["Reason"])
      resp.raise_for_status()
      
   # extract language from answer
   return resp.text



## --------------------------------------------------------------
##           MAIN
## --------------------------------------------------------------

# read text from stdin
text = sys.stdin.readline()

while text != '' :
   # clean text from newlines and trailing spaces.
   text = text.strip()
   
   # find out text language 
   resp = call_server(langid, {'text' : text})
   lang = json.loads(resp)["language"]
   
   # get predicate task label analysis from freeling
   resp = call_server(freeling, {'text' : text,
                                 'language' : lang,
                                 'OutputLevel' : 'shallow'})
   # print result.
   print(resp)

   # next line, if any
   text = sys.stdin.readline()
