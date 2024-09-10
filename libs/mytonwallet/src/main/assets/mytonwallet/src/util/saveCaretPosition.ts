export function saveCaretPosition(context: HTMLElement, decimals: number) {
  const selection = window.getSelection();
  if (!selection || selection.rangeCount === 0) {
    return undefined;
  }

  const range = selection.getRangeAt(0);
  range.setStart(context, 0);
  const clearedValue = range.toString().match(new RegExp(`(\\d+(?:[.,]\\d{0,${decimals}})?)`));
  const len = clearedValue?.[0]?.length || range.toString().length || 0;

  return function restore() {
    try {
      const { node, position } = getTextNodeAtPosition(context, len);

      selection.removeAllRanges();
      const newRange = new Range();
      newRange.setStart(node, position);
      newRange.setEnd(node, position);
      selection.addRange(newRange);
    } catch (e) {
      // ignore
    }
  };
}

function getTextNodeAtPosition(root: HTMLElement, index: number) {
  const NODE_TYPE = NodeFilter.SHOW_TEXT;
  const treeWalker = document.createTreeWalker(root, NODE_TYPE, (elem) => {
    if (index > elem.textContent!.length) {
      index -= elem.textContent!.length;
      return NodeFilter.FILTER_REJECT;
    }

    return NodeFilter.FILTER_ACCEPT;
  });

  const node = treeWalker.nextNode();

  return {
    node: node || root,
    position: index,
  };
}
