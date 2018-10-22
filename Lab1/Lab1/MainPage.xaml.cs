using System;
using Plugin.LocalNotifications;
using Xamarin.Forms;

namespace Lab1
{
    public partial class MainPage : ContentPage
    {
        public MainPage()
        {
            InitializeComponent();
        }

        void PushNotifBtnClicked(object sender, EventArgs e)
        {
            CrossLocalNotifications.Current.Show(
                title: "Very notification",
                body: "Much notification",
                id: 100,
                notifyTime: DateTime.Now.AddSeconds(5)
            );
        }
    }
}
