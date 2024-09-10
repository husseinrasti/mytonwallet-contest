#!/usr/bin/env bash

set -e

rm -f MyTonWallet-firefox-sources.tgz

COPYFILE_DISABLE=1 tar \
  --exclude='*.zip' \
  --exclude='*.tgz' \
  --exclude=./dist \
  --exclude=./dist-electron \
  --exclude=./node_modules \
  --exclude=./trash \
  --exclude=./.DS_Store \
  --exclude=./.idea \
  --exclude=./mobile \
  "$@" -cvzf /tmp/MyTonWallet-firefox-sources.tgz ./

mv /tmp/MyTonWallet-firefox-sources.tgz ./
