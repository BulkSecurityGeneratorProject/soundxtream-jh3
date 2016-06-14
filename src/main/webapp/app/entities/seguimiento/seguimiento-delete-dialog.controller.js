(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('SeguimientoDeleteController',SeguimientoDeleteController);

    SeguimientoDeleteController.$inject = ['$uibModalInstance', 'entity', 'Seguimiento'];

    function SeguimientoDeleteController($uibModalInstance, entity, Seguimiento) {
        var vm = this;

        vm.seguimiento = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Seguimiento.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
