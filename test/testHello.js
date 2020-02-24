var expect  = require('chai').expect;
const sayHello = require('../index.js')

it("Says Hello properly", () => {
    expect(sayHello("Mike")).to.be.equal('Hello Mike');
})
