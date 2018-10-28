using System;
using System.Collections.Generic;
using Plugin.Media.Abstractions;
using Xamarin.Forms;

namespace Lab1
{
    public partial class CameraCapturePreview : ContentPage
    {
        public CameraCapturePreview()
        {
            InitializeComponent();
        }

        public void SetPreviewImg(MediaFile mediaFile)
        {
            CaptureImgBuf.Source = ImageSource.FromStream(mediaFile.GetStream);
        }
    }
}
