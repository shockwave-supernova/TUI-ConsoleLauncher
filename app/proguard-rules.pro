-keep public class com.tui.next.commands.main.raw.** { *; }
-keep public class com.tui.next.commands.main.specific.** { *; }
-keep public class com.tui.next.commands.tuixt.raw.** { *; }
-keep public class com.tui.next.tuils.GenericFileProvider { *; }
-keep public class com.tui.next.tuils.PrivateIOReceiver { *; }
-keep public class com.tui.next.tuils.PublicIOReceiver { *; }
-keep class com.tui.next.managers.** { *; }
-keep class com.tui.next.tuils.libsuperuser.**
-keep class com.tui.next.managers.suggestions.HideSuggestionViewValues
-keep public class it.andreuzzi.comparestring2.**

-dontwarn com.tui.next.commands.main.raw.**

-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-dontwarn org.htmlcleaner.**
-dontwarn com.jayway.jsonpath.**
-dontwarn org.slf4j.**

-dontwarn org.jdom2.**
