import axios from 'axios';
import type { TonClientParameters } from '@ton/ton/dist/client/TonClient';
import { TonClient as TonCoreClient } from '@ton/ton/dist/client/TonClient';

import type { GetAddressInfoResponse } from '../types';

import { DEFAULT_ERROR_PAUSE, DEFAULT_RETRIES } from '../../../../config';
import axiosRetry from '../../../../lib/axios-retry';
import { fetchWithRetry } from '../../../../util/fetch';
import { logDebug } from '../../../../util/logs';

axiosRetry(axios, {
  retries: DEFAULT_RETRIES,
  shouldResetTimeout: true,
  retryDelay: (retryCount) => {
    return retryCount * DEFAULT_ERROR_PAUSE;
  },
  onRetry: (retryNumber, error, requestConfig) => {
    logDebug(`Retry request #${retryNumber}:`, requestConfig.url, error.response?.status);
  },
});

type Parameters = TonClientParameters & {
  headers?: AnyLiteral;
};

export class TonClient extends TonCoreClient {
  private initParameters: Parameters;

  constructor(parameters: Parameters) {
    super(parameters);
    this.initParameters = parameters;
  }

  getWalletInfo(address: string) {
    return this.callRpc('getWalletInformation', { address });
  }

  getAddressInfo(address: string): Promise<GetAddressInfoResponse> {
    return this.callRpc('getAddressInformation', { address });
  }

  callRpc(method: string, params: any): Promise<any> {
    return this.sendRequest(this.parameters.endpoint, {
      id: 1, jsonrpc: '2.0', method, params,
    });
  }

  async sendFile(src: Buffer | string): Promise<void> {
    const boc = typeof src === 'object' ? src.toString('base64') : src;
    await this.callRpc('sendBocReturnHashNoError', { boc });
  }

  async sendRequest(apiUrl: string, request: any) {
    const method: string = request.method;

    const headers: AnyLiteral = {
      ...this.initParameters.headers,
      'Content-Type': 'application/json',
    };
    if (this.parameters.apiKey) {
      headers['X-API-Key'] = this.parameters.apiKey;
    }
    const body = JSON.stringify(request);

    const response = await fetchWithRetry(apiUrl, {
      method: 'POST',
      body,
      headers,
    }, {
      shouldSkipRetryFn: (message, statusCode) => isNotTemporaryError(method, message, statusCode),
    });

    const data = await response.json();

    return data.result;
  }
}

function isNotTemporaryError(method: string, message?: string, statusCode?: number) {
  return Boolean(statusCode === 422 || message?.match(/(exit code|exitcode=|duplicate message)/));
}
