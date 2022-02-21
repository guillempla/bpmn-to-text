import json
from json import JSONDecodeError

import requests


def call_server(url, request):
    # Set query elements
    request_headers = {'Content-Type': 'application/json; charset=utf-8'}
    request_data = json.dumps(request, ensure_ascii=False).encode('utf-8')

    # Send request and get response
    resp = requests.post(url, data=request_data, headers=request_headers)
    # HTTP error, raise exception
    if resp.status_code != requests.codes.ok:
        print("ERROR", resp.status_code, "- Reason:", resp.headers["Reason"])
        resp.raise_for_status()

    # Extract language from answer
    return resp.text


class FreelingClient:
    def __init__(self, file_path, host="172.17.0.2", lang_port="50005", freeling_port="60006", lang="en",
                 resp_path="../freeling_responses/"):
        self.file_path = file_path
        self.host = host
        self.lang_port = lang_port
        self.freeling_port = freeling_port
        self.lang = lang
        self.responses_path = resp_path
        self.lang_url = "http://" + host + ":" + lang_port
        self.freeling_url = "http://" + host + ":" + freeling_port
        self.file_name = self.file_path.name.replace(".json", "")
        self.json_object = self.read_json_file()

    def send_to_freeling(self):
        wrapper = self.json_object[self.file_name]

        for attribute, value in wrapper.items():
            sentence = value["name"]
            # Get predicate task label analysis from freeling
            resp = call_server(self.freeling_url, {'text': sentence,
                                                   'language': self.lang,
                                                   'OutputLevel': 'shallow'})
            try:
                json_resp = json.loads(resp.replace("[", "").replace("]", ""))
                json_resp.pop("sentence")
            except JSONDecodeError:
                json_resp = {}
            wrapper[attribute].update(json_resp)

        # Save to disk
        self.save_to_disk(wrapper)

    def read_json_file(self):
        file = open(self.file_path)
        json_object = json.load(file)
        file.close()
        return json_object

    def save_to_disk(self, response):
        with open(self.responses_path + self.file_name + ".json", 'w') as out_file:
            json.dump(response, out_file)
        out_file.close()
