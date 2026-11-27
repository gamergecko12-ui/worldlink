# WorldLink

Fabric-Mod fuer Minecraft 26.3. Teile eine Singleplayer-Welt mit Freunden.

## Module

- `shared/` - Freundes-, Einladungs- und Sitzungslogik ohne Minecraft. Enthaelt die Tests.
- `worldlink-fabric/` - Client-Mod: Freigabe-Button, Freundesmenue.

## Bauen

    gradle wrapper
    gradle :shared:test
    gradle :worldlink-fabric:build
    gradle :worldlink-fabric:runClient

## Stand

- Phase 1: Welt freigeben und schliessen ueber den ESC-Button.
- Phase 2a: Freundesmenue mit Einladungscodes, Transport noch lokal.
- Offen: echter Tunnel (Phase 2b), Cloud mit Konten (Phase 3), Mod-Sync (Phase 4).
