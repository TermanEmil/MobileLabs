using System;
using Plugin.LocalNotifications;
using Plugin.Media;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
//using

[assembly: XamlCompilation(XamlCompilationOptions.Compile)]
namespace Lab1
{
    public partial class App : Application
    {
        public App()
        {
            InitializeComponent();
            MainPage = new NavigationPage(new MainPage());
        }

        protected override void OnStart()
        {
        }

        protected override void OnSleep()
        {
            // Handle when your app sleeps
        }

        protected override void OnResume()
        {
            // Handle when your app resumes
        }
    }
}
