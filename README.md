# SonyStyle Equalizer
An independent Sony-Walkman-inspired Android equalizer prototype.

## Included
- Dark music-player-inspired interface
- Native Android Equalizer band sliders
- Flat, Bass, Vocal and Bright presets
- GitHub Actions APK build workflow

## Compatibility limitation
The app attempts Android audio session `0` (output mix). Android and OEM firmware may reject, ignore, or restrict global audio effects. System-wide processing is not guaranteed on the Redmi 9 Power or OnePlus Nord CE 3 Lite. Some devices require vendor support, privileged system integration, or root.

## Build
Open **Actions → Build APK → Run workflow** in GitHub. Download the `SonyStyleEqualizer-debug` artifact when the run completes. It contains an installable debug-signed test APK, not a Play Store release build.

This independent project is not affiliated with Sony and contains no Sony proprietary source code or assets.
