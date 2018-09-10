This tool will access a public ESPN league and scrape the teams and players for a given week.
The scraped data will be written into simple HTML that can be pasted into a blogger website.
Simple heuristics are used to find 'interesting' stats from a week to be written into the blog.

I am learning Python while writing this, so thanks to the following authors:
* https://blog.hartleybrody.com/web-scraping/
* https://github.com/pcsforeducation/fantasy

## Contributing
1) Install python 3.7+ (https://www.python.org/downloads/)
2) Run `python -m pip install -r requirements.txt`
3) Create ffautoblogger.ini file from the template with correct leagueId, seasonId and week
4) Execute autoblogger.py `python autoblogger.py`
