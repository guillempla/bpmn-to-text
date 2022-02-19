import json

import requests


def read_file_lines(file_path):
    file = open(file_path, 'r')
    lines = file.readlines()
    file.close()
    return lines


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


def save_to_disk(name, response):
    with open(name + ".json", 'w') as out_file:
        out_file.write(response)
    out_file.close()


class FreelingClient:
    def __init__(self, file_path, host="172.17.0.2", lang_port="50005", freeling_port="60006", lang="en",
                 resp_path="../freeling_responses/"):
        self.file_path = file_path
        self.host = host
        self.lang_port = lang_port
        self.freeling_port = freeling_port
        self.lang = lang
        self.lang_url = "http://" + host + ":" + lang_port
        self.freeling_url = "http://" + host + ":" + freeling_port
        self.responses_path = resp_path
        self.lines = read_file_lines(self.file_path)

    def send_parsed_texts(self):
        final_response = ""

        for line in self.lines:
            # Clean text from newlines and trailing spaces.
            line = line.strip()

            # Get predicate task label analysis from freeling
            resp = call_server(self.freeling_url, {'text': line,
                                                   'language': self.lang,
                                                   'OutputLevel': 'shallow'})
            final_response = final_response + resp
        # Save to disk
        save_to_disk(self.responses_path + self.file_path.name.replace(".txt", ""), final_response)
