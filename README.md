# ZOMA XIT Android starter

## Features
- `ENTR` opens Happ Proxy (`com.happproxy`) and offers Google Play if missing.
- `IMPORT CONFIG FILE TO ENTR` opens Android's document picker, then tries to open the selected file as JSON. If Happ doesn't accept the file intent, it opens Happ so the user can import manually.
- `free fire` opens Free Fire (`com.dts.freefireth`) or tries Free Fire MAX (`com.dts.freefiremax`).
- Demo activation keys: `ZOMA-1H-2026` (one hour after activation), `ZOMA-PERM-2026` (permanent locally).

## Build APK with GitHub Actions
Upload the project contents to a new GitHub repository. Push to `main` or run the workflow manually from Actions. Download the `ZOMA-XIT-debug-apk` artifact.

## Important limitations
The activation keys are hard-coded demo keys and are not secure; production needs a backend. File import depends on Android and Happ's supported file formats; if direct file opening is unsupported, the app opens Happ for manual import. The app does not contain a working proxy server or a real proxy configuration. Happ requires a valid configuration/subscription/server of your own. This project does not modify Free Fire files.
