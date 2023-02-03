# zmqj
zeromq java binder

#linux
cmake -H. -Bbuild -DENABLE_DRAFTS=ON 
cmake --build build --config Release


# windows
cmake -G "Visual Studio 17 2022" -A x64 -Bbuild -DENABLE_DRAFTS=ON
cmake --build build --config Release
