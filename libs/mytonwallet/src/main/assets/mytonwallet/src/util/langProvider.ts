import type { TeactNode } from '../lib/teact/teact';

import type { LangCode, LangPack, LangString } from '../global/types';

import { IS_PACKAGED_ELECTRON, LANG_CACHE_NAME, LANG_LIST } from '../config';
import renderText from '../global/helpers/renderText';
// @ts-ignore this file is autogenerated
import defaultLangPackJson from '../i18n/en.json';
import * as cacheApi from './cacheApi';
import { createCallbackManager } from './callbacks';
import { formatInteger } from './formatNumber';
import { DEFAULT_LANG_CODE } from './windowEnvironment';

const defaultLangPack: LangPack = defaultLangPackJson;

export interface LangFn {
  (key: string): string;

  (key: string, value: any, format?: 'i', pluralValue?: number): string | TeactNode[];

  isRtl?: boolean;
  code?: LangCode;
  langName?: string;
}

const {
  addCallback,
  removeCallback,
  runCallbacks,
} = createCallbackManager();

export { addCallback, removeCallback };

const SUBSTITUTION_REGEX = /%\d?\$?[sdf@]/g;
const cache = new Map<string, string>();
let langPack: LangPack | undefined;
let currentLangCode: string | undefined;

function createLangFn() {
  return ((key: string, value?: any, format?: 'i', pluralValue?: number) => {
    if (value !== undefined && (typeof value !== 'object' || Array.isArray(value))) {
      const cacheValue = Array.isArray(value) ? JSON.stringify(value) : value;
      const cached = cache.get(`${key}_${cacheValue}_${format}${pluralValue ? `_${pluralValue}` : ''}`);
      if (cached) {
        return cached;
      }
    }

    if (!langPack && !defaultLangPack) {
      return key;
    }

    const langString = (langPack?.[key]) || (defaultLangPack?.[key]) || key;

    return processTranslation(langString, key, value, format, pluralValue);
  }) as LangFn;
}

// eslint-disable-next-line import/no-mutable-exports
export let getTranslation: LangFn = createLangFn();

export async function setLanguage(langCode: LangCode, callback?: NoneToVoidFunction) {
  if (langPack && langCode === currentLangCode) {
    if (callback) {
      callback();
    }

    return;
  }

  let newLangPack = await cacheApi.fetch(LANG_CACHE_NAME, langCode);

  if (!newLangPack) {
    newLangPack = await fetchRemote(langCode);
    if (!newLangPack) {
      return;
    }
  }

  cache.clear();

  currentLangCode = langCode;
  langPack = newLangPack;
  document.documentElement.lang = langCode;

  const langInfo = LANG_LIST?.find((l) => l.langCode === langCode);
  getTranslation = createLangFn();
  getTranslation.isRtl = Boolean(langInfo?.rtl);
  getTranslation.code = langCode.replace('-raw', '') as LangCode;
  getTranslation.langName = langInfo?.nativeName;

  if (callback) {
    callback();
  }

  runCallbacks();
}

export function clearPreviousLangpacks() {
  const langCachePrefix = LANG_CACHE_NAME.replace(/\d+$/, '');
  const langCacheVersion = Number((LANG_CACHE_NAME.match(/\d+$/) || [0])[0]);
  for (let i = 0; i < langCacheVersion; i++) {
    void cacheApi.clear(`${langCachePrefix}${i === 0 ? '' : i}`);
  }
}

async function fetchRemote(langCode: string): Promise<LangPack | undefined> {
  if (langCode === DEFAULT_LANG_CODE) {
    return defaultLangPack;
  }

  const response = await fetch(`${IS_PACKAGED_ELECTRON ? '.' : '..'}/i18n/${langCode}.json`);

  if (!response.ok) {
    const message = `An error has occured: ${response.status}`;
    throw new Error(message);
  }

  const remote = await response.json();

  if (remote) {
    await cacheApi.save(LANG_CACHE_NAME, langCode, remote);
    return remote;
  }

  return undefined;
}

function processTemplate(template: string, value: any) {
  value = Array.isArray(value) ? value : [value];
  const translationSlices = template.split(SUBSTITUTION_REGEX);
  const initialValue = translationSlices.shift();

  return translationSlices.reduce((result, str, index) => {
    return `${result}${String(value[index] ?? '')}${str}`;
  }, initialValue || '');
}

function processTemplateJsx(template: string, value: Record<string, TeactNode>) {
  return template.split('%')
    .reduce((acc, slice) => acc.concat(value[slice] ? [value[slice]] : renderText(slice)), [] as TeactNode[]);
}

function processTranslation(
  langString: LangString | string | undefined, key: string, value?: any, format?: 'i', pluralValue?: number,
) {
  const template = typeof langString === 'string' ? langString : langString?.value;
  if (!template || !template.trim() || value === undefined) {
    return template;
  }

  const formattedValue = format === 'i' ? formatInteger(value) : value;
  const result = typeof value === 'object' && !Array.isArray(value)
    ? processTemplateJsx(template, formattedValue)
    : processTemplate(template, formattedValue);
  if (typeof value !== 'object' && typeof result === 'string') {
    const cacheValue = Array.isArray(value) ? JSON.stringify(value) : value;
    cache.set(`${key}_${cacheValue}_${format}${pluralValue ? `_${pluralValue}` : ''}`, result);
  }

  return result;
}
