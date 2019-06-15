using Acr.UserDialogs;
using Android.App;
using Android.Content.PM;
using Android.OS;
using Xamarin.Forms;

namespace ToDoApp.Droid
{
    [Activity(Label = "ToDoApp", Icon = "@mipmap/icon", Theme = "@style/MainTheme", MainLauncher = true, ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation)]
    public class MainActivity : Xamarin.Forms.Platform.Android.FormsAppCompatActivity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            TabLayoutResource = Resource.Layout.Tabbar;
            ToolbarResource = Resource.Layout.Toolbar;

            base.OnCreate(savedInstanceState);

            Forms.Init(this, savedInstanceState);
            UserDialogs.Init(() => this);
            LoadApplication(new App());
        }
    }
}