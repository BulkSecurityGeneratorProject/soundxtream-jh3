(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('TrackDialogController', TrackDialogController);

    TrackDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Track', 'User', 'Style'];

    function TrackDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Track, User, Style) {
        var vm = this;

        vm.track = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
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

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
