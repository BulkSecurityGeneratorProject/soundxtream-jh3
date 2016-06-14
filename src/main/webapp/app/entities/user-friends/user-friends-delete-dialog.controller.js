(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('User_friendsDeleteController',User_friendsDeleteController);

    User_friendsDeleteController.$inject = ['$uibModalInstance', 'entity', 'User_friends'];

    function User_friendsDeleteController($uibModalInstance, entity, User_friends) {
        var vm = this;

        vm.user_friends = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            User_friends.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
