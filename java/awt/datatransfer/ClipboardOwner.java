package java.awt.datatransfer;

public interface ClipboardOwner {
  void lostOwnership(Clipboard paramClipboard, Transferable paramTransferable);
}
