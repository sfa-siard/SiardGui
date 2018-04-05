package ch.enterag.utils.fx;

import javafx.scene.Node;
import javafx.scene.Parent;

public abstract class FxUtils
{
  public static void dumpNode(String sIndent, Node node)
  {
    System.out.println(sIndent+node.getClass().getName()+": "+node.getId());
    if (node instanceof Parent)
    {
      Parent parent = (Parent)node;
      for (int i = 0; i < parent.getChildrenUnmodifiable().size(); i++)
      {
        Node nodeChild = parent.getChildrenUnmodifiable().get(i);
        dumpNode(sIndent+"  ",nodeChild);
      }
    }
  } /* dumpNode */
}
