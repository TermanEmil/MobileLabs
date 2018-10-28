using System;
using System.Threading.Tasks;
using Plugin.LocalNotifications;
using Plugin.Media;
using Plugin.Media.Abstractions;
using Plugin.Permissions;
using Plugin.Permissions.Abstractions;
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
            Task.Factory.StartNew(() =>
            {
                CrossLocalNotifications.Current.Show(
                    title: "Very notification",
                    body: "Much notification",
                    id: 100,
                    notifyTime: DateTime.Now.AddSeconds(5)
                );
            });
        }

        void GoogleSearchBtnClicked(object sender, EventArgs e)
        {
            var searchStr =
                "https://www.google.com/search?q=" +
                googleSearchEntry.Text.Replace(' ', '+');

            Task.Factory.StartNew(() =>
            {
                Device.OpenUri(new Uri(searchStr));
            });
        }

        async void TakePhoto(object sender, EventArgs eventArgs)
        {
            await CrossMedia.Current.Initialize();
            if (!await GetCameraPermission())
                return;

            if (!CrossMedia.Current.IsCameraAvailable ||
                !CrossMedia.Current.IsTakePhotoSupported)
            {
                await DisplayAlert("No Camera", "No camera available", "Not cool...");
                return;
            }

            var frontOrBackCamera = cameraSwitch.IsToggled ?
                                                Plugin.Media.Abstractions.CameraDevice.Rear :
                                                Plugin.Media.Abstractions.CameraDevice.Front;

            var imgStoreOptions = new Plugin.Media.Abstractions.StoreCameraMediaOptions()
            {
                Directory = "Pictures",
                Name = "test.jpg",
                DefaultCamera = frontOrBackCamera
            };

            MediaFile capturedPhoto;
            try
            {
                capturedPhoto = await CrossMedia.Current.TakePhotoAsync(imgStoreOptions);
            }
            catch (Exception)
            {
                await DisplayAlert("Failed operation", "Couldn't take a photo.", "Ok");
                return;
            }

            if (capturedPhoto == null)
                return;

            var preView = new CameraCapturePreview();
            preView.SetPreviewImg(capturedPhoto);

            await Navigation.PushAsync(preView);
        }

        async void PickPhoto(object sender, EventArgs eventArgs)
        {
            if (!CrossMedia.Current.IsPickPhotoSupported)
            {
                await DisplayAlert("Not supported", "It's not possible to pickup a photo", "Ok");
                return;
            }

            var photo = await CrossMedia.Current.PickPhotoAsync();
            if (photo == null)
                return;

            var preView = new CameraCapturePreview();
            preView.SetPreviewImg(photo);
            await Navigation.PushAsync(preView);
        }

        async Task<bool> GetCameraPermission()
        {
            try
            {
                var status = await CrossPermissions.Current.CheckPermissionStatusAsync(Permission.Camera);
                if (status != PermissionStatus.Granted)
                {
                    if (await CrossPermissions.Current.ShouldShowRequestPermissionRationaleAsync(Permission.Camera))
                    {
                        await DisplayAlert("Permissions", "Access to camera is needed", "Gotcha");
                    }

                    var results = await CrossPermissions.Current.RequestPermissionsAsync(Permission.Camera);
                    if (results.ContainsKey(Permission.Camera))
                    {
                        status = results[Permission.Camera];
                    }
                }

                if (status == PermissionStatus.Granted)
                    return true;
                else
                {
                    await DisplayAlert("Permissions not granted", "Can not continue, try again.", "ok... :(");
                    return false;
                }
            }
            catch (Exception)
            {
                return false;
            }
        }

        async Task<bool> GetGalleryPermission()
        {
            try
            {
                var status = await CrossPermissions.Current.CheckPermissionStatusAsync(Permission.Camera);
                if (status != PermissionStatus.Granted)
                {
                    if (await CrossPermissions.Current.ShouldShowRequestPermissionRationaleAsync(Permission.Camera))
                    {
                        await DisplayAlert("Permissions", "Access to camera is needed", "Gotcha");
                    }

                    var results = await CrossPermissions.Current.RequestPermissionsAsync(Permission.Camera);
                    if (results.ContainsKey(Permission.Camera))
                    {
                        status = results[Permission.Camera];
                    }
                }

                if (status == PermissionStatus.Granted)
                    return true;
                else
                {
                    await DisplayAlert("Permissions not granted", "Can not continue, try again.", "ok... :(");
                    return false;
                }
            }
            catch (Exception)
            {
                return false;
            }
        }
    }
}
