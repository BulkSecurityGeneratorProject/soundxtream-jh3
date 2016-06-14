(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('User_activityDialogController', User_activityDialogController);

    User_activityDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'User_activity', 'User', 'Track', 'Playlist'];

    function User_activityDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, User_activity, User, Track, Playlist) {
        var vm = this;

        vm.user_activity = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();
        vm.tracks = Track.query();
        vm.playlists = Playlist.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.user_activity.id !== null) {
                User_activity.update(vm.user_activity, onSaveSuccess, onSaveError);
            } else {
                User_activity.save(vm.user_activity, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('soundxtream3App:user_activityUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.dateLiked = false;
        vm.datePickerOpenStatus.sharedDate = false;
        vm.datePickerOpenStatus.uploadDate = false;
        vm.datePickerOpenStatus.createdDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
