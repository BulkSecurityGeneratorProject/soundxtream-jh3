(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('PlaylistDialogController', PlaylistDialogController);

    PlaylistDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Playlist', 'Track', 'User'];

    function PlaylistDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Playlist, Track, User) {
        var vm = this;

        vm.playlist = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.tracks = Track.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.playlist.id !== null) {
                Playlist.update(vm.playlist, onSaveSuccess, onSaveError);
            } else {
                Playlist.save(vm.playlist, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('soundxtream3App:playlistUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.dateCreated = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
