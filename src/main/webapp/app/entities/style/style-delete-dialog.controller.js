(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('StyleDeleteController',StyleDeleteController);

    StyleDeleteController.$inject = ['$uibModalInstance', 'entity', 'Style'];

    function StyleDeleteController($uibModalInstance, entity, Style) {
        var vm = this;

        vm.style = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Style.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
