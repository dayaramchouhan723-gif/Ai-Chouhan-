"""CHOUHAN STAR v2.2.8 — Windows PC Connect Companion
Author: Dayaram Chouhan
Offline-first desktop companion exposing local PC Connect status and ZARA Hinglish bridge.
"""

from pathlib import Path
import json
import platform
import time


ASSET_PATH = Path(__file__).resolve().parent.parent / "assets" / "zara.png"


def get_system_status() -> dict:
    return {
        "app": "CHOUHAN STAR PC Companion",
        "version": "2.2.8",
        "owner": "Dayaram Chouhan",
        "host": platform.node(),
        "os": f"{platform.system()} {platform.release()}",
        "avatar_asset_exists": ASSET_PATH.exists(),
        "avatar_asset": str(ASSET_PATH.name),
        "timestamp": int(time.time()),
    }


def main() -> None:
    status = get_system_status()
    print("=== CHOUHAN STAR PC CONNECT v2.2.8 ===")
    print(json.dumps(status, indent=2))
    print("Status: READY — Open PC 12 Grid inside CHOUHAN STAR Android app to pair.")


if __name__ == "__main__":
    main()
