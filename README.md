# ImageUploadSoftGroup

## Image Upload App Documentation

## Overview
The Image Upload Component simplifies the process of selecting and uploading images in Android applications.
It offers a user-friendly interface for choosing images from the device's gallery or capturing pictures using the camera.
Additionally, users can preview the selected image before initiating the upload process.

## Features

### Image Source Options
The Image Upload Component supports two primary options for selecting an image:

1. **Gallery:** Users can choose an image from the device gallery.
2. **Camera:** Users can capture a picture using the device camera.

### Preview Functionality
Once an image is selected, users can preview it by tapping the "Preview" button.
This feature allows users to confirm their selection before proceeding with the upload.

### Permissions Handling
The component takes care of runtime permissions required for accessing the device's storage and camera.

### Network State Check
Before initiating the image upload process, the component performs a network state check.
This ensures that the device has a stable internet connection, preventing potential issues during the upload.

## Additional Features

### Image Compression

The `compressImage` function is provided to compress images before performing operations such as uploading to enhance efficiency and reduce file size.

### MVVM Architecture
The Image Upload Component is implemented using the MVVM (Model-View-ViewModel) architecture to achieve separation of concerns and maintain a clean code structure. The architecture consists of the following components:

* **Model:** Represents the data and business logic.
* **View:** Represents the UI components and user interface.
* **ViewModel:** Acts as a mediator between the Model and View, handling UI-related logic.

### Cloudinary API Integration
To enhance image upload capabilities, the Image Upload Component integrates seamlessly with the Cloudinary API. This allows for seamless and efficient image storage and retrieval.
The `uploadImage` function has been updated to utilize the Cloudinary API for a more robust image upload process.

## Usage Note

* **Loader Display:**
    - Upon tapping the "Submit" button, a loading indicator is displayed to simulate the image upload process.
    - The message 'Image uploaded successfully' is displayed after a successful image submission to the Cloudinary API.