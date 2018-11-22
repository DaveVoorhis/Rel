#!/bin/sh
echo 'Build swt_linux'
pushd ../swtNative/swt_linux
ant -S
popd
cp ../swtNative/swt_linux/lib/* ../_Deployment/lib/swt/linux_64

echo 'Build swt_macos'
pushd ../swtNative/swt_macos
ant -S
popd
cp ../swtNative/swt_macos/lib/* ../_Deployment/lib/swt/macos_64

echo 'Build swt_win'
pushd ../swtNative/swt_win
ant -S
popd
cp ../swtNative/swt_win/lib/* ../_Deployment/lib/swt/win_64

echo 'Build Shared'
pushd ../Shared
ant -S
popd
cp ../Shared/lib/* ../_Deployment/lib/rel

echo 'Build ServerV0000'
pushd ../ServerV0000
ant -S
popd
cp ../ServerV0000/lib/* ../_Deployment/lib/rel

echo 'Build Server'
pushd ../Server
ant -S
popd
cp ../Server/lib/* ../_Deployment/lib/rel

echo 'Build Client'
pushd ../Client
ant -S
popd
cp ../Client/lib/* ../_Deployment/lib/rel

echo 'Build DBrowser'
pushd ../DBrowser
ant -S
popd
cp ../DBrowser/lib/* ../_Deployment/lib/rel

echo 'Build Tests'
pushd ../Tests
ant -S
popd
cp ../Tests/lib/* ../_Deployment/lib/rel
