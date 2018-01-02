This tool will access a public ESPN league and scrape the teams and players for a given week.
The scraped data will be written into simple HTML that can be pasted into a blogger website.
Simple heuristics are used to find 'interesting' stats from a week to be written into the blog.

I am learning Python while writing this, so thanks to the following authors:
* https://blog.hartleybrody.com/web-scraping/
* https://github.com/pcsforeducation/fantasy

## Install Packages
* Requests: http://docs.python-requests.org/en/master/
* Beautiful Soup: https://www.crummy.com/software/BeautifulSoup/

Install python 3.4+ (it has pip installed by default) and then run `python -m pip install <bla>`

## Post-season weirdness
The post-season is set up differently from the regular season. So the run_playoffs script was created. Keep in mind that ESPN's website changes the format of the players tables after the entire season is over. So the player.py had different clubhouse schemas for these two cases.
