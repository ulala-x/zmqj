set(EXECUTABLE_OUTPUT_PATH ${CMAKE_BINARY_DIR}/bin)
set(LIBRARY_OUTPUT_PATH  ${CMAKE_BINARY_DIR}/lib)

set(ZMQJ_SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/Socket.cpp")
set(ZMQJ_HEADERS "${CMAKE_CURRENT_SOURCE_DIR}/Socket.h")
set(ZMQJ_INCLUDES "./" "${CMAKE_BINARY_DIR}/configured_files/include")

set(JAVA_AWT_LIBRARY NotNeeded)
set(JAVA_JVM_LIBRARY NotNeeded)
set(JAVA_INCLUDE_PATH2 NotNeeded)
set(JAVA_AWT_INCLUDE_PATH NotNeeded)

find_package(Java REQUIRED)
find_package(JNI REQUIRED)

#-----------------------------------------------------------------------------
# platform specifics

add_definitions(
    -DWIN32
	-DDLL_EXPORT
	-DFD_SETSIZE=1024
)

set(java-sources
    ../../../lib/src/main/java/org/zeromq/*.java
)
set(javah-headers
    ${CMAKE_CURRENT_BINARY_DIR}/org_zeromq_ZMQ.h
	${CMAKE_CURRENT_BINARY_DIR}/org_zeromq_ZMQ_Context.h
	${CMAKE_CURRENT_BINARY_DIR}/org_zeromq_ZMQ_Socket.h
)

add_custom_command(
	OUTPUT ${javah-headers}
	COMMAND ${Java_JAVAC_EXECUTABLE} -h . ${java-sources}	
	WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}
	DEPENDS ${java-classes}
)



add_library(${ZMQJ} SHARED ${ZMQJ_SOURCES}  ${ZMQJ_HEADERS} ${javah-headers})
target_include_directories(${ZMQJ} PUBLIC ${ZMQJ_INCLUDES} ${JNI_INCLUDE_DIRS})
target_link_libraries(
    ${ZMQJ}
    libzmq
)
