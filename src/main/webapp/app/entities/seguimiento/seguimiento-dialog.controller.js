(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('SeguimientoDialogController', SeguimientoDialogController);

    SeguimientoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Seguimiento', 'User'];

    function SeguimientoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Seguimiento, User) {
        var vm = this;

        vm.seguimiento = entity;
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
            if (vm.seguimiento.id !== null) {
                Seguimiento.update(vm.seguimiento, onSaveSuccess, onSaveError);
            } else {
                Seguimiento.save(vm.seguimiento, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('soundxtream3App:seguimientoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.followingDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
