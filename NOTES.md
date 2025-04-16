Configure:

```fish
bash configure --with-debug-level=release --with-jvm-variants=server --with-extra-cflags='-march=native -mtune=native -pipe -w -Wno-error-use-after-free' --with-boot-jdk=(path normalize (dirname (which java))/..)
```

Fix permission denied error on macOS:

```fish
sudo xattr -dr com.apple.provenance .
```

Build:

```fish
CONF=macosx-x86_64-server-release make images
```
