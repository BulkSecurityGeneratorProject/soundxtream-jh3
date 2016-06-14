(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('User_friendsDialogController', User_friendsDialogController);

    User_friendsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'User_friends', 'User'];

    function User_friendsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, User_friends, User) {
        var vm = this;

        vm.user_friends = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.user_friends.id !== null) {
                User_friends.update(vm.user_friends, onSaveSuccess, onSaveError);
            } else {
                User_friends.save(vm.user_friends, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('soundxtream3App:user_friendsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.friendshipDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
