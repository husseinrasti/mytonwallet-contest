const fs = require('fs');

const {
  EDGE_PRODUCT_ID,
  EDGE_CLIENT_ID,
  EDGE_CLIENT_SECRET,
  EDGE_ACCESS_TOKEN_URL,
  EDGE_FILE_PATH = './MyTonWallet-chrome.zip',
  EDGE_NOTES_PATH,
} = process.env;

if (
  !EDGE_PRODUCT_ID
  || !EDGE_CLIENT_ID
  || !EDGE_CLIENT_SECRET
  || !EDGE_ACCESS_TOKEN_URL
  || !EDGE_FILE_PATH
) {
  console.error('Missing env vars!')
  process.exit(1);
}

(async () => {
  const { EdgeAddonsAPI } = await import('@plasmohq/edge-addons-api');

  const client = new EdgeAddonsAPI({
    productId: EDGE_PRODUCT_ID,
    clientId: EDGE_CLIENT_ID,
    clientSecret: EDGE_CLIENT_SECRET,
    accessTokenUrl: EDGE_ACCESS_TOKEN_URL,
  });

  let notes = undefined;
  if (EDGE_NOTES_PATH && await fs.promises.access(file, fs.constants.F_OK)) {
    notes = await fs.promises.readFile(EDGE_NOTES_PATH);
  }

  const operationId = await client.submit({
    filePath: EDGE_FILE_PATH,
    notes,
  });

  console.log('Publish operation ID:', operationId);
})();
