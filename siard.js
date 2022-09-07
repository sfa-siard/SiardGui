/*====================================================================== 
Global script for manual pages of SIARD Suite 2. 
Application: SIARD Suite 2 Manual
Description: Global script for manual pages of SIARD Suite 2.
Platform   : ECMAScript 8
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2017
Created    : 20.12.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/

/*--------------------------------------------------------------------*/
/** replace all <image>.png by <image>.w.png if the platform is Windows.
 */
function adaptImages()
{
  if (navigator.appVersion.indexOf("Win") >= 0)
  {
    aimg = document.getElementsByTagName("img");
    for (var i = 0; i < aimg.length; i++)
    {
      var img = aimg[i];
      var sSource = img.getAttribute("src");
      if (sSource.endsWith(".png") && 
          !sSource.startsWith("..") && 
          !sSource.startsWith("/") && 
          !sSource.startsWith("http"))
      {
        sTarget = sSource.substring(0,sSource.length-".png".length)+".w.png";
        img.src = sTarget;
      }
    }
  }
}; /* adaptImages */
