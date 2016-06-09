(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('TrackDialogController', TrackDialogController);

    TrackDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Track', 'User', 'Style'];

    function TrackDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Track, User, Style) {
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

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.track.id !== null) {
                Track.update(vm.track, onSaveSuccess, onSaveError);
            } else {
                Track.save(vm.track, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
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
