<h4 style="color: #5e9ca0;"><span style="color: #000000;">Adnet SDK</span></h4>
<p style="text-align: left;">This SDK is only for Conjoinix personal use. which is working for Image/Web/Video &nbsp; Ad</p>
<pre class="kode code-toolbar  language-css"><br /><span style="color: #333333;">ConjoinixAd.Builder(this).adKey(" your ad key ").</span><span style="color: #333333;">build { isSuccess, message -&gt;  {  </span><span style="font-family: Verdana, Arial, Helvetica, sans-serif;">}  }</span></pre>
<p>&nbsp;</p>
<p>Complete hit with enormous param with&nbsp;default values</p>
<pre class="kode code-toolbar  language-css"><span style="color: #333333;">ConjoinixAd.Builder(this)</span> 
<span style="color: #333333;">.zoneID( "0" )&nbsp;&nbsp;</span>
<span style="color: #333333;">.adType( "SPLASH" )&nbsp;&nbsp;</span>
<span style="color: #333333;">.screenType( "SPLASH" ) <span style="caret-color: #999999;">&nbsp;</span></span><br /><span style="color: #333333;">.adKey("your_ad_id").</span><span style="color: #333333;">build { isSuccess, message -&gt;</span>
<p><span style="color: #333333;">&nbsp; &nbsp; &nbsp;}</span></p>
</pre>
 
<p><strong>Gradle Configuration:</strong></p>
<pre class="kode code-toolbar  language-css"><code id="depCodeGradle" class=" kode  language-css"></code>implementation 'com.github.developerConjoinix:adnetSDK:1.2'</pre>
<div class="kode code-toolbar  language-css"><span style="font-family: Verdana, Arial, Helvetica, sans-serif;">Add the following lines in you project level Gradle file</span></div>
<pre>allprojects { <br />repositories    { <br />maven { <br />url 'https://jitpack.io' <br />     } <br />  }<br />}<br />&nbsp;</pre>
<p><strong>Thanks and Happy Coding ;)&nbsp;</strong></p>
<p>&nbsp;</p>
