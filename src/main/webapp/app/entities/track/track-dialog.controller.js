(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('TrackDialogController', TrackDialogController);

    TrackDialogController.$inject = ['$log', '$timeout', '$scope', '$stateParams', 'User_activity', '$uibModalInstance', 'DataUtils', 'entity', 'Track', 'User', 'Style'];

    function TrackDialogController ($log, $timeout, $scope, $stateParams, User_activity, $uibModalInstance, DataUtils, entity, Track, User, Style) {
        var vm = this;

        vm.track = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.users = User.query();
        vm.styles = Style.query();
        vm.user_activity = {
            "createdDate": null,
            "id": null,
            "track": null,
            "type": "uploadTrack",
            "uploadDate": null,
            "user": null
        };

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.track.id !== null) {
                Track.update(vm.track, onUpdateSuccess, onSaveError);
            } else {
                Track.save(vm.track, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('soundxtream3App:trackUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
            vm.user_activity = {
                "track": result,
                "type": "uploadTrack",
                "uploadDate": result.date_upload,
                "user": result.user
            };
            User_activity.save(vm.user_activity, function (res) {
                $log.log(res);
            });
        }

        function onUpdateSuccess (result) {
            $scope.$emit('soundxtream3App:trackUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date_upload = false;

        vm.setArtwork = function ($file, track) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        track.artwork = base64Data;
                        track.artworkContentType = $file.type;
                    });
                });
            }
        };

        vm.setVisual = function ($file, track) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        track.visual = base64Data;
                        track.visualContentType = $file.type;
                    });
                });
            }
        };

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
