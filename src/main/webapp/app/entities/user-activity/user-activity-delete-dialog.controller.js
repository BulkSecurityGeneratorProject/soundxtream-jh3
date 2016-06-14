(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('User_activityDeleteController',User_activityDeleteController);

    User_activityDeleteController.$inject = ['$uibModalInstance', 'entity', 'User_activity'];

    function User_activityDeleteController($uibModalInstance, entity, User_activity) {
        var vm = this;

        vm.user_activity = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            User_activity.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
