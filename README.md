<h4><span style="color: #000000;">Adnet SDK</span></h4>
<p>This SDK is only for Conjoinix personal use. which is working for Image/Web/Video  Ad</p>
<pre class="kode code-toolbar  language-css"><br /><span>ConjoinixAd.Builder(this).adKey(" your ad key ").</span>build { isSuccess, message -&gt;  {  </span><span style="font-family: Verdana, Arial, Helvetica, sans-serif;">}  }</span></pre>
<p>Complete hit with enormous param with&nbsp;default values</p>
<pre class="kode code-toolbar  language-css"><span style="color: #333333;">ConjoinixAd.Builder(this)</span> 
<span style="color: #333333;">.zoneID( "0" )&nbsp;&nbsp;</span>
<span style="color: #333333;">.adType( "SPLASH" )&nbsp;&nbsp;</span>
<span style="color: #333333;">.screenType( "SPLASH" ) <span style="caret-color: #999999;">&nbsp;</span></span><br /><span style="color: #333333;">.adKey("your_ad_id").</span><span style="color: #333333;">build { isSuccess, message -&gt;</span></pre>
<p><strong>Banner Slider MultipleAd with Viewpager Configuration:</strong></p>
<p>add XML Tag in activity_xml</p>
<pre>&lt;com.conjoinix.adsdk.BannerView<br />     android:visibility="gone"<br />    android:id="@+id/bannerView"<br />    android:layout_width="match_parent"<br />    app:adKey="app_ad_key"<br />    android:layout_height="200dp"/&gt;<br /><br /><br /></pre>
<pre>bannerView.loadAd(this){ isLoaded -&gt;<br /><br />    if(isLoaded)  {<br />        bannerView.show()<br />    }}</pre>
 
<pre class="kode code-toolbar  language-css">&nbsp;</pre>
<p><strong>Gradle Configuration:</strong></p>
<pre class="kode code-toolbar  language-css"><code id="depCodeGradle" class=" kode  language-css"></code>implementation 'com.github.developerConjoinix:adnetSDK:1.2'</pre>
<div class="kode code-toolbar  language-css"><span style="font-family: Verdana, Arial, Helvetica, sans-serif;">Add the following lines in you project level Gradle file</span></div>
<pre>allprojects { <br />repositories    { <br />maven { <br />url 'https://jitpack.io' <br />     } <br />  }<br />}<br />&nbsp;</pre>
<p><strong>Thanks and Happy Coding ;)&nbsp;</strong></p>
<p>&nbsp;</p>
