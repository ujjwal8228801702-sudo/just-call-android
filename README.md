# JUST CALL — Premium Android Dialer

A native Android SIM dialer starter project with a dark titanium / warm-gold visual theme.

## Included
- Phone-number entry and SIM call launch through Android Telecom
- SIM selection hand-off when multiple call-capable accounts are present
- Contact name/number search
- Android default-dialer role request
- In-call screen with answer, reject, end, mute and speaker controls
- Custom JUST CALL vector launcher icon
- GitHub Actions workflow to build a debug APK

## Build APK
Every push to `main` starts the workflow in **Actions → Build JUST CALL APK**. When it succeeds, open the run and download the `just-call-debug-apk` artifact.

## Important Android limitations
- The user must grant permissions and approve JUST CALL as the default phone app in Android settings.
- SIM routing and call UI behavior vary by device manufacturer and Android version; test on the target phone.
- Third-party apps generally cannot record cellular call audio reliably on modern Android. Recording depends on device/OEM support, applicable law, and consent. This project does not secretly record calls and does not claim universal call-recording support.
- This is an initial buildable starter, not yet a fully tested Play Store-ready dialer. Validate incoming/outgoing calls on a real device before daily use.
