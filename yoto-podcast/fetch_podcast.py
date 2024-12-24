import argparse
import feedparser
import json
import os
import re
import requests


def read_podcast_episodes_from_rss(rss_url: str, episode_type: str) -> dict:
    try:
        data = feedparser.parse(rss_url)
        print(f'Loading {len(data.entries)} episodes from "{data.feed.title}".')
        episodes = []
        for index, entry in enumerate(data.entries):
            if "enclosures" not in entry or len(entry.enclosures) == 0:
                print(f"Skipping entry with missing audio: {entry.title}!")
                continue

            if (
                episode_type
                and "itunes_episodetype" in entry
                and episode_type != entry["itunes_episodetype"]
            ):
                print(
                    f"Skipping entry \"{entry.title}\" with episode type {entry.get('itunes_episodetype')}"
                )
                continue

            enclosure = entry.enclosures[0]
            episodes.append(
                {
                    "title": entry.title,
                    "file_name": title_to_filename(index, entry.title),
                    "published": entry.published,
                    "description": entry.summary,
                    "audio_url": enclosure.href,
                    "audio_type": enclosure.type,
                }
            )

        return episodes
    except Exception as e:
        print(f"Error parsing RSS feed: {e}")
        return []


def title_to_filename(index: int, title: str, ext: str = ".mp3") -> str:
    # replace non-word characters with underscores
    sanitized = re.sub(r"\W", "_", title)
    # replace multiple underscores with a single underscore
    sanitized = re.sub(r"_+", "_", sanitized)
    # remove leading/trailing underscores
    sanitized = sanitized.strip("_")
    # add the index to the beginning of the filename
    return f"{index:03}_{sanitized}{ext}"


def download_audio(episode: dict, output_dir: str):
    try:
        url = episode["audio_url"]
        file_name = episode["file_name"]
        print(f"Downloading {episode['file_name']}...")

        response = requests.get(url, stream=True)
        response.raise_for_status()

        file_path = os.path.join(output_dir, file_name)

        with open(file_path, "wb") as audio_file:
            for chunk in response.iter_content(chunk_size=8192):
                audio_file.write(chunk)

        print(f"Downloaded: {file_path}")
    except requests.RequestException as e:
        print(f"Error downloading {url}: {e}")


def main():
    parser = argparse.ArgumentParser(
        description="Download audio files from an RSS feed."
    )

    parser.add_argument("rss_url", help="The URL of the RSS feed.")
    parser.add_argument(
        "-o",
        "--output",
        default="downloads",
        help="The directory to save downloaded files (default: downloads).",
    )
    parser.add_argument(
        "-t",
        "--episode-type",
        default=None,
        help='Podcast type to download. E.g. "full", "bonus", or "trailer". Default is download all episodes.',
    )
    args = parser.parse_args()

    rss_url = args.rss_url
    output_dir = args.output
    episode_type = args.episode_type

    os.makedirs(output_dir, exist_ok=True)

    print("Fetching and parsing RSS feed...")
    episodes = read_podcast_episodes_from_rss(rss_url, episode_type)
    if not episodes:
        print("No audio files found in the RSS feed.")
        return

    episodes_file = os.path.join(output_dir, "_episodes.json")
    with open(episodes_file, "w") as f:
        json.dump(episodes, f, indent=3)

    print(f"Found {len(episodes)} audio files. Starting downloads...")
    for episode in episodes:
        download_audio(episode, output_dir)


if __name__ == "__main__":
    main()
