package d.sfischer.Seahaven2;

public class AppPackageNameMarshal<Appname, PackageName> {


    private String appname;
    private String packageName;

    AppPackageNameMarshal ( String appname, String packageName ) {
        this.appname = appname;
        this.packageName = packageName;
    }

    String getAppname ( ) {
        return appname;
    }

    String getPackageName ( ) {
        return packageName;
    }

    @Override
    public int hashCode ( ) {
        return appname.hashCode () ^ packageName.hashCode ();
    }


    @Override
    public boolean equals ( Object o ) {
        if (! ( o instanceof AppPackageNameMarshal )) return false;
        AppPackageNameMarshal pairo = (AppPackageNameMarshal) o;
        return this.appname.equals (pairo.getAppname ()) &&
                this.packageName.equals (pairo.getPackageName ());
    }

}
