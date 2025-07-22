cmake_minimum_required(VERSION 3.18.1)

# 添加Opus库
add_library(opus SHARED
            IMPORTED)

set_target_properties(opus
                      PROPERTIES IMPORTED_LOCATION
                      ${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI}/libopus.so)

# 添加JNI接口
add_library(voiceai-opus SHARED
            native-lib.cpp)

find_library(log-lib log)

target_link_libraries(voiceai-opus
                      opus
                      ${log-lib})