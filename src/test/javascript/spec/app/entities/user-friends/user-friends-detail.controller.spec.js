'use strict';

describe('Controller Tests', function() {

    describe('User_friends Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockUser_friends, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockUser_friends = jasmine.createSpy('MockUser_friends');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'User_friends': MockUser_friends,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("User_friendsDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'soundxtream3App:user_friendsUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
