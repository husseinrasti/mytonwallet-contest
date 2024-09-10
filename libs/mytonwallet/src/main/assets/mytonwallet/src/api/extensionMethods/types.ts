import type * as extensionMethods from './extension';
import type * as legacyDappMethods from './legacy';
import type * as siteMethods from './sites';

export type ExtensionMethods = typeof extensionMethods;
export type ExtensionMethodArgs<N extends keyof ExtensionMethods> = Parameters<ExtensionMethods[N]>;
export type ExtensionMethodResponse<N extends keyof ExtensionMethods> = ReturnType<ExtensionMethods[N]>;

export type SiteMethods = typeof siteMethods;
export type SiteMethodArgs<N extends keyof SiteMethods> = Parameters<SiteMethods[N]>;
export type SiteMethodResponse<N extends keyof SiteMethods> = ReturnType<SiteMethods[N]>;

export type LegacyDappMethods = typeof legacyDappMethods;
export type LegacyDappMethodArgs<N extends keyof LegacyDappMethods> = Parameters<LegacyDappMethods[N]>;
export type LegacyDappMethodResponse<N extends keyof LegacyDappMethods> = ReturnType<LegacyDappMethods[N]>;
