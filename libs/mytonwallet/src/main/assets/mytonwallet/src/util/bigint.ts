import { ONE_TON } from '../config';
import { fromDecimal } from './decimals';
import { randomBytes } from './random';

export const BIGINT_PREFIX = 'bigint:';

export function bigintReviver(this: any, key: string, value: any) {
  if (typeof value === 'string' && value.startsWith(BIGINT_PREFIX)) {
    return BigInt(value.slice(7));
  }
  return value;
}

export function bigintAbs(value: bigint) {
  return value === -0n || value < 0n ? -value : value;
}

export function bigintDivideToNumber(value: bigint, num: number) {
  return (value * ONE_TON) / fromDecimal(num);
}

export function bigintMultiplyToNumber(value: bigint, num: number) {
  return (value * fromDecimal(num)) / ONE_TON;
}

export function bigintRandom(bytes: number) {
  let value = BigInt(0);
  for (const randomNumber of randomBytes(bytes)) {
    const randomBigInt = BigInt(randomNumber);
    // eslint-disable-next-line no-bitwise
    value = (value << BigInt(8)) + randomBigInt;
  }
  return value;
}

export function bigintCountBits(value: bigint) {
  const binaryString = value.toString(2);
  return binaryString.length;
}
